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

  editStarted(transaction) {
    console.log(transaction)
    this.editedId = transaction.id
    this.edited = {
      from: transaction.from.toString(),
      to: transaction.to.toString(),
      on: moment(transaction.on).toDate(),
      amount: parseFloat(transaction.amount),
      description: transaction.description
    }
  }

  editSaved() {
    this.resourceService.updateTransaction(this.editedId, this.edited, transaction => {

      this.transactions = this.transactions.map(t => {
        if (t.id == this.editedId) {
          return this.decorateTransactionWithAccountName(transaction)
        } else {
          return t
        }
      })
      this.editedId = "none"
      this.edited = null
    })
  }

  fetchTransactions() {
    let month = (this.selectedMonth == "") ? null : this.selectedMonth
    let account = (this.selectedAccount == "") ? null : this.selectedAccount
    this.resourceService.getTransactions(month, account, transactions =>
      this.transactions = transactions.map(t => this.decorateTransactionWithAccountName(t))
    )
  }

  decorateTransactionWithAccountName(t) {
    t.fromName = this.accounts.find(a => a.id == t.from).name
    t.toName = this.accounts.find(a => a.id == t.to).name
    return t
  }
}
