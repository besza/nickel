class AccountsController {
  constructor(ResourceService) {
    this.ResourceService = ResourceService
    this.resetInput()
    this.ResourceService.getAllAccounts(accounts =>
      this.accounts = accounts
    )
  }

  createAccount() {
    this.ResourceService.createAccount(this.input, account => {
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
