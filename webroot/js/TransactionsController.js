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
    this.resourceService.getTransactionsInMonth(this.selectedMonth, transactions =>
      this.transactions = transactions
    )
  }
}
