angular
  .module('nickel', ['ngRoute'])
  .config(function($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')

    $routeProvider
      .when('/new-transaction', {
        template: '<new-transaction></new-transaction>'
      })
      .when('/accounts', {
        template: '<accounts></accounts>'
      })
      .when('/transactions', {
        template: '<transactions></transactions>'
      })
      .otherwise('/new-transaction')
  })
  .factory('ResourceService', ResourceService)
  .component('accounts', {
    controller: AccountsController,
    templateUrl: 'template/accounts.html'
  })
  .component('newTransaction', {
    controller: NewTransactionController,
    templateUrl: 'template/new-transaction.html'
  })
  .component('transactions', {
    controller: TransactionsController,
    templateUrl: 'template/transactions.html'
  })
