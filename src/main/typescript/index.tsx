import * as React from "react"
import * as ReactDOM from "react-dom"
import { HashRouter as Router, Route, Link } from 'react-router-dom'

import AccountPage from "./Pages/AccountPage"
import NewTransactionPage from "./Pages/NewTransactionPage"
import TransactionPage from "./Pages/TransactionPage"

ReactDOM.render(
  <Router>
    <div>
      <ul>
        <li><Link to="/new-transaction">New transaction</Link></li>
        <li><Link to="/accounts">Accounts</Link></li>
        <li><Link to="/transactions">Transactions</Link></li>
      </ul>
      <hr />
      <Route exact path="/" component={NewTransactionPage} />
      <Route path="/new-transaction" component={NewTransactionPage} />
      <Route path="/accounts" component={AccountPage} />
      <Route path="/transactions" component={TransactionPage} />
    </div>
  </Router>,
  document.getElementById("root")
)
