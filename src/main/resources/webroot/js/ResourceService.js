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

  getTransactions(month, account, onSuccess) {
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
    this.http.post('api/transactions', this.formatTransaction(transaction)).then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  updateTransaction(id, transaction, onSuccess) {
    this.http.put('api/transactions/' + id, this.formatTransaction(transaction)).then(
      response => onSuccess(response.data),
      this.onFailure
    )
  }

  deleteTransaction(id, onSuccess) {
    this.http.delete('api/transactions/' + id).then(
      response => onSuccess(),
      this.onFailure
    )
  }

  formatTransaction(transaction) {
    return {
      from: parseInt(transaction.from),
      to: parseInt(transaction.to),
      on: moment(transaction.on).format("YYYY-MM-DD"),
      amount: transaction.amount.toFixed(2),
      description: transaction.description.trim()
    }
  }
}
