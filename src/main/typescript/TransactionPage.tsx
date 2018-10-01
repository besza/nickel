import * as React from "react";

import * as AccountApi from "./Api/AccountApi"
import * as TransactionApi from "./Api/TransactionApi"
import { Account, Id, Transaction } from "./Models"

interface State {
  transactions: Transaction[],
  accounts: Account[],
  selectedAccountId?: Id<Account>,
  months: string[],
  selectedMonth?: string
}

export default class TransactionPage extends React.Component<{}, State> {
  constructor(props: {}) {
    super(props)
    this.state = { transactions: [], accounts: [], months: [] }
  }

  componentDidMount(): void {
    AccountApi.getAll().then(accounts =>
      this.setState({ accounts: accounts })
    )
    TransactionApi.getMonths().then(months => {
      const lastMonth = months.length > 0
        ? months[months.length - 1]
        : undefined
      this.setState({ months: months, selectedMonth: lastMonth })
      TransactionApi.get(lastMonth).then(transactions =>
        this.setState({ transactions: transactions })
      )
    })
  }

  private monthSelected = (event: React.ChangeEvent<HTMLSelectElement>): void => {
    const selectedMonth = event.target.value === "" ? undefined : event.target.value
    this.setState({ selectedMonth: selectedMonth })
    TransactionApi.get(selectedMonth, this.state.selectedAccountId).then(transactions =>
      this.setState({ transactions: transactions })
    )
  }

  private accountSelected = (event: React.ChangeEvent<HTMLSelectElement>): void => {
    const selectedAccountId = event.target.value === "" ? undefined : event.target.value
    this.setState({ selectedAccountId: selectedAccountId })
    TransactionApi.get(this.state.selectedMonth, selectedAccountId).then(transactions =>
      this.setState({ transactions: transactions })
    )
  }

  private deleteTransaction = (id: Id<Transaction>): void => {
    TransactionApi.del(id).then(_ => {
      const remainingTransactions = this.state.transactions.filter(t => t.id !== id)
      this.setState({ transactions: remainingTransactions })
    })
  }

  private findAccountName = (id: Id<Account>): string => {
    const account = this.state.accounts.find(account => account.id === id)
    return account == null ? "" : account.name
  }

  render() {
    return (
      <div>
        <select onChange={this.monthSelected} value={this.state.selectedMonth}>
          <option value="">All</option>
          {
            this.state.months.map(month =>
              <option key={month} value={month}>{month}</option>
            )
          }
        </select>
        <select onChange={this.accountSelected} value={this.state.selectedAccountId}>
          <option value="">All</option>
          {
            this.state.accounts.map(account =>
              <option key={account.id} value={account.id}>{account.name}</option>
            )
          }
        </select>
        <table>
          <thead>
            <tr>
              <th>Day of transaction</th>
              <th>From</th>
              <th>To</th>
              <th>Amount</th>
              <th>Description</th>
              <th>Time of entry</th>
            </tr>
          </thead>
          <tbody>
            {
              this.state.transactions.map(transaction =>
                <tr key={transaction.id}>
                  <td>{transaction.on}</td>
                  <td>{this.findAccountName(transaction.from)}</td>
                  <td>{this.findAccountName(transaction.to)}</td>
                  <td>{transaction.amount}</td>
                  <td>{transaction.description}</td>
                  <td>{transaction.createdAt}</td>
                  <td><button onClick={(_) => this.deleteTransaction(transaction.id)}>Delete</button></td>
                </tr>
              )
            }
          </tbody>
        </table>
      </div>
    )
  }
}
