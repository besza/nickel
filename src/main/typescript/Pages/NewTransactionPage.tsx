import * as React from "react";

import * as AccountApi from "../Api/AccountApi"
import * as TransactionApi from "../Api/TransactionApi"
import { Account, NewTransaction } from "../Models"

interface State {
  accounts: Account[],
  newTransaction: NewTransaction
}

export default class TransactionPage extends React.Component<{}, State> {
  constructor(props: {}) {
    super(props)
    this.state = { accounts: [], newTransaction: this.makeInitialTransaction([]) }
  }

  componentDidMount(): void {
    AccountApi.getAll().then(accounts =>
      this.setState({
        accounts: accounts,
        newTransaction: this.makeInitialTransaction(accounts)
      })
    )
  }

  private makeInitialTransaction(accounts: Account[]): NewTransaction {
    return {
      from: accounts.length === 0 ? "" : accounts[0].id.toString(),
      to: accounts.length === 0 ? "" : accounts[0].id.toString(),
      on: new Date().toISOString().split("T")[0],
      amount: "",
      description: ""
    }
  }

  private createTransaction = (event: React.FormEvent<any>): void => {
    event.preventDefault()
    const newTransaction = this.state.newTransaction
    const sanitized = {
      ...newTransaction,
      amount: parseFloat(newTransaction.amount).toFixed(2),
      description: newTransaction.description.trim()
    }
    TransactionApi.create(sanitized).then(_ => {
      this.setState({ newTransaction: this.makeInitialTransaction(this.state.accounts) })
    })
  }

  private editTransactionField = (field: keyof NewTransaction, event: any) => {
    const current = this.state.newTransaction
    if (current != null) {
      let updated = { ...current }
      updated[field] = event.target.value.toString()
      this.setState({ newTransaction: updated })
    }
  }

  render() {
    return (
      <div>
        <form onSubmit={this.createTransaction}>
          <div className="col-lg-6 form-group">
            <label>From</label>
            <select className="form-control"
              onChange={(e) => this.editTransactionField("from", e)}
              value={this.state.newTransaction.from}>
              {
                this.state.accounts.map(account =>
                  <option key={account.id} value={account.id}>{account.name}</option>
                )
              }
            </select>
          </div>

          <div className="col-lg-6 form-group">
            <label>To</label>
            <select className="form-control"
              onChange={(e) => this.editTransactionField("to", e)}
              value={this.state.newTransaction.to}>
              {
                this.state.accounts.map(account =>
                  <option key={account.id} value={account.id}>{account.name}</option>
                )
              }
            </select>
          </div>

          <div className="col-lg-6 form-group">
            <label>Date of transaction</label>
            <input type="date" className="form-control"
              value={this.state.newTransaction.on}
              onChange={(e) => this.editTransactionField("on", e)} />
          </div>

          <div className="col-lg-6 form-group">
            <label>Amount</label>
            <input type="number" step="0.01" className="form-control"
              value={this.state.newTransaction.amount}
              onChange={(e) => this.editTransactionField("amount", e)} />
          </div>

          <div className="col-lg-6 form-group">
            <label>Description</label>
            <input className="form-control"
              value={this.state.newTransaction.description}
              onChange={(e) => this.editTransactionField("description", e)} />
          </div>

          <div className="col-lg-6 form-group">
            <button className="btn-lg btn-primary">Create</button>
          </div>
        </form>
      </div>
    )
  }
}
