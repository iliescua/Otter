import React, { Component } from "react";

//Default snack obj that will store attributes of a given snack from backend
class Snack {
  constructor(id, name, stock, capacity, orderAmnt, cost) {
    this.id = id;
    this.name = name;
    this.stock = stock;
    this.capacity = capacity;
    this.orderAmnt = orderAmnt;
    this.cost = cost;
  }
}

class Challenge extends Component {
  //Create a constructor so state can be updated
  constructor(props) {
    super(props);
    this.getLowStock = this.getLowStock.bind(this);
    this.state = {
      snacks: [],
      totalCost: 0.0
    }
  }

  /** 
   * This function fetches the data from the backend and parses it
   * then it updates the current state
   */
  getLowStock = () => {
    fetch("http://localhost:4567/low-stock", {
      method: "get",
      headers: { 'Content-Type': 'application/json' }
    })
      .then(response => response.json())
      .then(data => {
        //Used so the list is cleared everytime the button is pressed to avoid constantly adding data
        this.setState({
          snacks: []
        });
        let nextState = this.state;
        for (let i = 0; i < data.length; i++) {
          const snack = new Snack(data[i].id, data[i].name, data[i].stock, data[i].capacity, data[i].orderAmnt, data[i].cost)
          nextState.snacks.push(snack);
        }
        this.setState(nextState);
      });
  }

  /**
   * Reads data from order amount input box and sets the approprate snacks order amount
   * to said value and updates the state
   * 
   * @param {event action from onChange command} e 
   * @param {*row index of the text input the user currently edited} i 
   */
  addOrder = (e, i) => {
    const orderAmnt = e.target.value;
    //Get list from state and find obj at specific index
    let snacks = this.state.snacks;
    let snack = snacks.at(i);
    //Make sure what the user entered is valid else set order amount to 0
    if (Number.isInteger(orderAmnt) && orderAmnt > 0) {
      snack.orderAmnt = orderAmnt;
    } else {
      snack.orderAmnt = orderAmnt;
    }
    //Set the updated obj back into the list and update state
    snacks[i] = snack;
    this.setState(snacks);
  }

  /**
   * Sends a JSON object to the backend using a post request then receives
   * back a JSON obj that contains a floating point value for the total reorder cost
   * this value is then used to update the state for the total cost
   */
  sendOrder = () => {
    if (this.state.snacks.length !== 0) {
      fetch("http://localhost:4567/restock-cost", {
        method: 'post',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.state.snacks)
      })
        .then(response => response.json())
        .then(cost => {
          this.setState({
            totalCost: cost
          })
        });
    } else {
      console.log("Add some data ");
    }
  }

  render() {
    return (
      <div>
        <table>
          <thead>
            <tr>
              <td>SKU</td>
              <td>Item Name</td>
              <td>Amount in Stock</td>
              <td>Capacity</td>
              <td>Order Amount</td>
            </tr>
          </thead>
          <tbody>
            {this.state.snacks.map((snack, index) =>
              <tr>
                <td>{snack.id}</td>
                <td>{snack.name}</td>
                <td>{snack.stock}</td>
                <td>{snack.capacity}</td>
                <td><input placeholder="Enter amount" onChange={(e) => this.addOrder(e, index)}></input></td>
              </tr>
            )}
          </tbody>
        </table>
        < div > Total Cost: {'$' + this.state.totalCost}</div >
        < button onClick={this.getLowStock} > Get Low - Stock Items</button >
        <button onClick={this.sendOrder}>Determine Re-Order Cost</button>
      </div >
    );
  }
}

export default Challenge;
