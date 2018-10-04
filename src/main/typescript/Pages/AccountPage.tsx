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
        <form className="form-inline mb-3" onSubmit={this.formSubmitted} >
          <input type="text" placeholder="Account name" className="form-control mr-2"
            value={this.state.newName}
            onChange={this.newNameChanged} />
          <button type="submit" className="btn btn-primary">Create</button>
        </form>

        <ul className="list-group">
          {
            this.state.accounts.map(account =>
              <li key={account.id} className="list-group-item">{account.name}</li>
            )
          }
        </ul>
      </div>
    )
  }
}
