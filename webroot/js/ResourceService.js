class ResourceService {
  constructor($http) {
    this.http = $http
  }

  getAllAccounts(onSuccess) {
    this.http.get('api/accounts').then(
      response => onSuccess(response.data),
      response => console.log(response)
    )
  }

  createAccount(account, onSuccess) {
    this.http.post('api/accounts', account).then(
      response => onSuccess(response.data),
      response => console.log(response)
    )
  }
}
