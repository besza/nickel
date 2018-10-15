import * as React from "react";

import * as AccountApi from "../Api/AccountApi"
import { Account, Id, MonthlyBalance } from "../Models"

interface State {
  readonly summarize: boolean,
  readonly accounts: Account[],
  readonly selectedAccountId?: Id<Account>,
  readonly monthlyBalances: MonthlyBalance[]
  readonly accountIdsInBalances: Id<Account>[]
}

export default class AccountPage extends React.Component<{}, State> {
  constructor(props: {}) {
    super(props)
    this.state = { summarize: true, accounts: [], monthlyBalances: [], accountIdsInBalances: [] }
  }

  componentDidMount(): void {
    AccountApi.getAll().then(accounts => {
      const firstAccountId = accounts.length > 0
        ? accounts[0].id
        : undefined
      this.setState({ accounts: accounts, selectedAccountId: firstAccountId })

      if (firstAccountId != null) {
        AccountApi.getBalances(firstAccountId).then(monthlyBalances => {
          const accountIdsInBalances = monthlyBalances.length > 0
            ? monthlyBalances[0].accountBalances.map(balance => balance[0])
            : []
          this.setState({ monthlyBalances: monthlyBalances, accountIdsInBalances: accountIdsInBalances })
        })
      }
    })
  }

  private summarizeChanged = (event: React.ChangeEvent<HTMLInputElement>): void => {
    const newSummarize = event.target.checked
    this.setState({ summarize: newSummarize })
  }

  private accountSelected = (event: React.ChangeEvent<HTMLSelectElement>): void => {
    const selectedAccountId = event.target.value === "" ? undefined : event.target.value
    this.setState({ selectedAccountId: selectedAccountId })
    if (selectedAccountId != null) {
      AccountApi.getBalances(selectedAccountId).then(monthlyBalances => {
        const accountIdsInBalances = monthlyBalances.length > 0
          ? monthlyBalances[0].accountBalances.map(balance => balance[0])
          : []
        this.setState({ monthlyBalances: monthlyBalances, accountIdsInBalances: accountIdsInBalances })
      })
    }
  }

  private findAccountName = (id: Id<Account>): string => {
    const account = this.state.accounts.find(account => account.id === id)
    return account == null ? "" : account.name
  }

  render() {
    return (
      <div>
        <select onChange={this.accountSelected} value={this.state.selectedAccountId}>
          {
            this.state.accounts.map(account =>
              <option key={account.id} value={account.id}>{account.name}</option>
            )
          }
        </select>
        <div className="form-check form-check-inline">
          <input className="form-check-input" type="checkbox" onChange={this.summarizeChanged} checked={this.state.summarize}></input>
          <label className="form-check-label">Summarize</label>
        </div>

        <table className="table table-sm">
          <thead>
            {
              this.state.summarize
                ? <tr>
                    <th>Month</th>
                    <th>Balance</th>
                    <th>In</th>
                    <th>Out</th>
                  </tr>
                : <tr>
                    <th>Month</th>
                    <th>Balance</th>
                    {
                      this.state.accountIdsInBalances.map(accountId =>
                        <th>{this.findAccountName(accountId)}</th>
                      )
                    }
                  </tr>
            }
          </thead>
          <tbody>
            {
              this.state.monthlyBalances.map(monthlyBalance =>
                this.state.summarize
                  ? <tr>
                      <td>{monthlyBalance.month}</td>
                      <td>{monthlyBalance.balance}</td>
                      <td>{monthlyBalance.in}</td>
                      <td>{monthlyBalance.out}</td>
                    </tr>
                  : <tr>
                      <td>{monthlyBalance.month}</td>
                      <td>{monthlyBalance.balance}</td>
                      {
                        monthlyBalance.accountBalances.map(accountBalance =>
                          <td>{accountBalance[1]}</td>
                        )
                      }
                    </tr>
              )
            }
          </tbody>
        </table>
      </div>
    )
  }
}
