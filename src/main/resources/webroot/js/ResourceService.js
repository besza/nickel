class ResourceService {
  constructor($http) {
    this.http = $http
    this.onFailure = response => console.log(response)
  }

  getAllAccounts(onSuccess) {
    this.http.get('api/accounts').then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  createAccount(account, onSuccess) {
    this.http.post('api/accounts', account).then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  getTransactionsInMonth(month, account, onSuccess) {
    this.http.get(
      'api/transactions',
      { params: { month: month, account: account } }
    ).then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  getTransactionMonths(onSuccess) {
    this.http.get('api/transactions/months').then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  createTransaction(transaction, onSuccess) {
    this.http.post('api/transactions', transaction).then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }
}
