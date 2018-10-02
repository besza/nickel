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
          <label>From</label>
          <select onChange={(e) => this.editTransactionField("from", e)} value={this.state.newTransaction.from}>
            {
              this.state.accounts.map(account =>
                <option key={account.id} value={account.id}>{account.name}</option>
              )
            }
          </select>

          <label>To</label>
          <select onChange={(e) => this.editTransactionField("to", e)} value={this.state.newTransaction.to}>
            {
              this.state.accounts.map(account =>
                <option key={account.id} value={account.id}>{account.name}</option>
              )
            }
          </select>

          <input type="date" value={this.state.newTransaction.on} onChange={(e) => this.editTransactionField("on", e)} />

          <input type="number" step="0.01" placeholder="Amount" value={this.state.newTransaction.amount} onChange={(e) => this.editTransactionField("amount", e)} />

          <input placeholder="Description" value={this.state.newTransaction.description} onChange={(e) => this.editTransactionField("description", e)} />

          <button type="submit">Create</button>
        </form>
      </div>
    )
  }
}
