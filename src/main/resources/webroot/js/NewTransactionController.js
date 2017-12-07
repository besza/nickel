class NewTransactionController {
  constructor(ResourceService) {
    this.resourceService = ResourceService
    this.resourceService.getAllAccounts(accounts =>
      this.accounts = accounts
    )
    this.resetInput()
  }

  createTransaction() {
    this.resourceService.createTransaction(this.input, transaction => {
      this.resetInput()
    })
  }

  resetInput() {
    this.input = {
      from: null,
      to: null,
      on: new Date(),
      amount: '',
      description: ''
    }
  }
}
