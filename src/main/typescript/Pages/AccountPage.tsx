import * as React from "react";

import * as AccountApi from "../Api/AccountApi"
import { Account } from "../Models"

interface State {
  readonly accounts: ReadonlyArray<Account>,
  readonly newName: string
}

export default class AccountPage extends React.Component<{}, State> {
  constructor(props: {}) {
    super(props)
    this.state = { accounts: [], newName: "" }
  }

  componentDidMount(): void {
    AccountApi.getAll().then(accounts =>
      this.setState({ accounts: accounts })
    )
  }

  private newNameChanged = (event: React.ChangeEvent<HTMLInputElement>): void => {
    const newName = event.target.value
    this.setState({ newName: newName })
  }

  private formSubmitted = (event: React.FormEvent<any>): void => {
    event.preventDefault()
    const newAccount = { name: this.state.newName }
    AccountApi.create(newAccount).then(createdAccount =>
      this.setState((prevState, {}) => ({
        accounts: prevState.accounts.concat([createdAccount]),
        newName: ""
      }))
    )
  }

  render() {
    return (
      <div>
        <ul> {
          this.state.accounts.map(account =>
            <li key={account.id}>{account.name}</li>
          )
        } </ul>
        <form onSubmit={this.formSubmitted} >
          <input value={this.state.newName} onChange={this.newNameChanged}
            type="text" placeholder="Account name" />
          <button type="submit">Create</button>
        </form>
      </div>
    )
  }
}
