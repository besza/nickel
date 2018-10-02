import * as React from "react";

import * as AccountApi from "../Api/AccountApi"
import * as TransactionApi from "../Api/TransactionApi"
import { Account, Id, NewTransaction, Transaction } from "../Models"

interface State {
  transactions: Transaction[],
  accounts: Account[],
  selectedAccountId?: Id<Account>,
  months: string[],
  selectedMonth?: string,
  editedTransaction?: EditedTransaction
}

interface EditedTransaction extends NewTransaction {
  readonly id: Id<Transaction>
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

  private editTransaction = (transaction: Transaction): void => {
    this.setState({ editedTransaction: { ...transaction } })
  }

  private cancelEditing = (): void => {
    this.setState({ editedTransaction: undefined })
  }

  private saveEditing = (): void => {
    const editedTransaction = this.state.editedTransaction
    if (editedTransaction != null) {
      TransactionApi.update(editedTransaction.id, editedTransaction).then(_ => {
        this.setState({ editedTransaction: undefined })
        TransactionApi.get(this.state.selectedMonth, this.state.selectedAccountId).then(transactions =>
          this.setState({ transactions: transactions })
        )
      })
    }
  }

  private findAccountName = (id: Id<Account>): string => {
    const account = this.state.accounts.find(account => account.id === id)
    return account == null ? "" : account.name
  }

  private ifEdited = (id: Id<Transaction>, onTrue: (x: EditedTransaction) => any, onFalse: () => any): any => {
    if (this.state.editedTransaction == null) {
      return onFalse()
    } else if (this.state.editedTransaction.id === id) {
      return onTrue(this.state.editedTransaction)
    } else {
      return onFalse()
    }
  }

  private editTransactionField = (field: keyof EditedTransaction, event: any) => {
    const current = this.state.editedTransaction
    if (current != null) {
      let updated = { ...current }
      updated[field] = event.target.value
      this.setState({ editedTransaction: updated })
    }
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
                this.ifEdited(
                  transaction.id,
                  (editedTransaction =>
                    <tr key={transaction.id}>
                      <td>
                        <input type="date" value={editedTransaction.on} onChange={(e) => this.editTransactionField("on", e)} />
                      </td>
                      <td>
                        <select onChange={(e) => this.editTransactionField("from", e)} value={editedTransaction.from}>
                          {
                            this.state.accounts.map(account =>
                              <option key={account.id} value={account.id}>{account.name}</option>
                            )
                          }
                        </select>
                      </td>
                      <td>
                        <select onChange={(e) => this.editTransactionField("to", e)} value={editedTransaction.to}>
                          {
                            this.state.accounts.map(account =>
                              <option key={account.id} value={account.id}>{account.name}</option>
                            )
                          }
                        </select>
                      </td>
                      <td>
                        <input type="number" step="0.01" value={editedTransaction.amount} onChange={(e) => this.editTransactionField("amount", e)} />
                      </td>
                      <td>
                        <input value={editedTransaction.description} onChange={(e) => this.editTransactionField("description", e)} />
                      </td>
                      <td>{transaction.createdAt}</td>
                      <td>
                        <button onClick={(_) => this.saveEditing()}>Save</button>
                        <button onClick={(_) => this.cancelEditing()}>Cancel</button>
                      </td>
                    </tr>
                  ),
                  (() =>
                    <tr key={transaction.id}>
                      <td>{transaction.on}</td>
                      <td>{this.findAccountName(transaction.from)}</td>
                      <td>{this.findAccountName(transaction.to)}</td>
                      <td>{transaction.amount}</td>
                      <td>{transaction.description}</td>
                      <td>{transaction.createdAt}</td>
                      <td>
                        <button onClick={(_) => this.deleteTransaction(transaction.id)}>Delete</button>
                        <button onClick={(_) => this.editTransaction(transaction)}>Edit</button>
                      </td>
                    </tr>
                  )
                )
              )
            }
          </tbody>
        </table>
      </div>
    )
  }
}
