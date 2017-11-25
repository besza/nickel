class AccountsController {
  constructor(ResourceService) {
    this.resourceService = ResourceService
    this.resetInput()
    this.resourceService.getAllAccounts(accounts =>
      this.accounts = accounts
    )
  }

  createAccount() {
    this.resourceService.createAccount(this.input, account => {
      this.accounts.push(account)
      this.resetInput()
    })
  }

  resetInput() {
    this.input = {
      name: ''
    }
  }
}
