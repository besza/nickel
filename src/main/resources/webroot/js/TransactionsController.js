class TransactionsController {
  constructor(ResourceService) {
    this.resourceService = ResourceService
    this.resourceService.getAllAccounts(accounts =>
      this.accounts = accounts
    )
    this.resourceService.getTransactionMonths(months => {
      this.months = months
      if (this.months.length > 0) {
        this.selectedMonth = months[months.length - 1]
        this.monthSelected()
      }
    })
  }

  monthSelected() {
    this.fetchTransactions()
  }

  accountSelected() {
    this.fetchTransactions()
  }

  fetchTransactions() {
    let account = (this.selectedAccount == "") ? null : this.selectedAccount
    this.resourceService.getTransactionsInMonth(this.selectedMonth, account, transactions =>
      this.transactions = transactions.map(t => {
        t.fromName = this.accounts.find(a => a.id == t.from).name
        t.toName = this.accounts.find(a => a.id == t.to).name
        return t
      })
    )
  }
}
