import * as React from "react"
import * as ReactDOM from "react-dom"

import AccountPage from "./AccountPage"
import TransactionPage from "./TransactionPage"

ReactDOM.render(
  <div>
    <AccountPage />
    <TransactionPage />
  </div>,
  document.getElementById("root")
)
