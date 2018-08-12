import * as React from "react";

interface State {
  readonly accounts: ReadonlyArray<Account>
}

interface Account {
  readonly id: string,
  readonly name: string
}

export default class AccountPage extends React.Component<{}, State> {
  constructor() {
    super({})
    this.state = { accounts: [] }
  }

  componentDidMount() {
    fetch("http://localhost:8081/api/accounts")
      .then(response => response.json())
      .then(json => this.setState((st, {}) => ({ accounts: json })))
      .catch(reason => console.log(reason))
  }

  render() {
    return (
      <ul> { 
        this.state.accounts.map(
          account => <li>{account.name}</li>
        )
      } </ul>
    )
  }
}