angular
  .module('nickel', ['ngRoute'])
  .config(function($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')

    $routeProvider
      .when('/accounts', {
        template: '<accounts></accounts>'
      })
      .otherwise('/accounts')
  })
  .factory('ResourceService', ResourceService)
  .component('accounts', {
    controller: AccountsController,
    templateUrl: 'template/accounts.html'
  })
