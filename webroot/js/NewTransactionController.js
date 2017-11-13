class NewTransactionController {
  constructor(ResourceService) {
    this.resourceService = ResourceService
    this.resourceService.getAllAccounts(accounts =>
      this.accounts = accounts
    )
    this.resetInput()
  }

  createTransaction() {
    this.resourceService.createTransaction(
      {
        from: parseInt(this.input.from),
        to: parseInt(this.input.to),
        on: moment(this.input.on).format("YYYY-MM-DD"),
        amount: this.input.amount.toFixed(2),
        description: this.input.description.trim()
      },
      transaction => {
        this.resetInput()
      }
    )
  }

  resetInput() {
    this.input = {
      from: null,
      to: null,
      on: new Date(),
      amount: 0,
      description: ''
    }
  }
}
