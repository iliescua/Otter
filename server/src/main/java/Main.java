import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import snack.Snack;

import static spark.Spark.*;

public class Main {
    private static final int NAME_CELL_NUM = 0;
    private static final int STOCK_CELL_NUM = 1;
    private static final int CAPACITY_CELL_NUM = 2;
    private static final int ID1_CELL_NUM = 3;
    private static final int ID2_CELL_NUM = 1;
    private static final int COST_CELL_NUM = 2;
    private static final double LARGE_COST = 100.0;
    private static final float REFILL_PERCENT = 0.25f;
    private static final int DEFAULT_ORDER_AMNT = 0;

    public static void main(String[] args) {
        HashMap<String, Snack> data = new HashMap<>();
        try {
            FileInputStream invFile = new FileInputStream("./resources/Inventory.xlsx");
            FileInputStream distFile = new FileInputStream("./resources/Distributors.xlsx");
            readFile(invFile, data, true);
            readFile(distFile, data, false);
        } catch (FileNotFoundException e) {
            System.out.println("Can't find file");
        }

//        This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                    response.header("Access-Control-Allow-Headers",
                            "content-type");

                    response.header("Access-Control-Allow-Methods",
                            "GET, POST");

                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        get("/low-stock", (request, response) -> {
            ArrayList<Snack> needsRestock = new ArrayList<>();
//            Use Gson library to turn ArrayList of obj into JSON format
            Gson gson = new Gson();
            for (String id : data.keySet()) {
//                Check if stock is less than 25% capacity
                if ((float) data.get(id).getStock() / (float) data.get(id).getCapacity() < REFILL_PERCENT) {
                    needsRestock.add(data.get(id));
                }
            }
            return gson.toJson(needsRestock);
        });

        post("/restock-cost", (request, response) -> {
            Gson gson = new Gson();
            Snack[] snacks = gson.fromJson(request.body(), Snack[].class);
            float totalCost = 0.0f;
            for (Snack snack : snacks){
                totalCost += (snack.getOrderAmnt() * snack.getCost());
            }
            totalCost = Math.round(totalCost * 100.0f)/100.0f;
            return gson.toJson(totalCost);
        });
    }

    /**
     * Reads in and parses excel data into a hashmap that uses the id as the key and creates
     * an object for the rest of the values which is the stored in the hashmap
     *
     * @param file FileInputStream for the Excel doc
     * @param data HashMap that will store all the parsed data
     * @param type Boolean used to denote which file was passed in
     */
    public static void readFile(FileInputStream file, HashMap<String, Snack> data, boolean type) {
        try {
            Workbook workbook = new XSSFWorkbook(file);
//            Iterate through however many sheets are present
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
//                Go through each row in the sheet
                for (Row row : sheet) {
//                    Ignore the first row because it just gives the column names
                    if (row.getRowNum() != 0) {
//                        Apache POI detects data about rows 10 which is null and bricks everything
//                        That is why this weird if statement looks like so
//                        This will be removed in future once excel is updated and bug disappears
                        if (sheet.getSheetName().equals("Candy Corp") && row.getRowNum() >= 10) {
                            System.out.println("Row:" + row.getRowNum() + " Data shouldn't be here yet its reading that there is");
//                            DO nothing here only a check to ensure everything doesn't break
                        } else {
//                            Type is a boolean denoting which file I am reading from
//                            Type == true then its inventory else it is distributors
                            if (type) {
//                                Grab data from each column storing it in an object
//                                Cost is given a ridiculously large value given the context, so it isn't null
                                String name = row.getCell(NAME_CELL_NUM).getStringCellValue();
                                int stock = (int) row.getCell(STOCK_CELL_NUM).getNumericCellValue();
                                int capacity = (int) row.getCell(CAPACITY_CELL_NUM).getNumericCellValue();
                                String id = Integer.toString((int) row.getCell(ID1_CELL_NUM).getNumericCellValue());
                                Snack snack = new Snack(name, stock, capacity, id, LARGE_COST, DEFAULT_ORDER_AMNT);
//                                Add the obj to a hashmap using the id as the key and the obj as the value
//                                This is done, so we can have quick lookup and manipulation later
                                data.put(id, snack);
                            } else {
                                String id = Integer.toString((int) row.getCell(ID2_CELL_NUM).getNumericCellValue());
                                double cost = row.getCell(COST_CELL_NUM).getNumericCellValue();
//                                Only record the cheapest cost
                                if (data.get(id).getCost() > cost) {
                                    data.get(id).setCost(cost);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading/parsing file");
        }
    }
}
