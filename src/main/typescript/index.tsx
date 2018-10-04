import * as React from "react"
import * as ReactDOM from "react-dom"
import { HashRouter as Router, Route, Link } from 'react-router-dom'

import 'bootstrap/dist/css/bootstrap.min.css'

import AccountPage from "./Pages/AccountPage"
import NewTransactionPage from "./Pages/NewTransactionPage"
import TransactionPage from "./Pages/TransactionPage"

ReactDOM.render(
  <Router>
    <div className="container-fluid">
      <div className="row h-100">
        <nav className="col-2 bg-light border-right" style={{ minHeight: "100vh" }}>
          <ul className="nav flex-column">
            <li className="nav-item"><span className="navbar-brand">Nickel</span></li>
            <li className="nav-item"><Link to="/new-transaction" className="nav-link">New transaction</Link></li>
            <li className="nav-item"><Link to="/accounts" className="nav-link">Accounts</Link></li>
            <li className="nav-item"><Link to="/transactions" className="nav-link">Transactions</Link></li>
          </ul>
        </nav>

        <div className="col-md-10 p-3">
          <Route exact path="/" component={NewTransactionPage} />
          <Route path="/new-transaction" component={NewTransactionPage} />
          <Route path="/accounts" component={AccountPage} />
          <Route path="/transactions" component={TransactionPage} />
        </div>
      </div>
    </div>
  </Router>,
  document.getElementById("root")
)
