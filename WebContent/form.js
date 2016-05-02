/**
 * 
 */
function UserController($scope, $http)
{
  $scope.user = {};
 prompt("aaa");
  $scope.createUser = function() 
  {
    $http({
      method: 'POST',
      url: 'http://localhost:8080/HirSalon/SalonServer',
      headers: {'Content-Type': 'application/json'},
      data:  $scope.user
    }).success(function (data) 
      {
    	$scope.status=data;
      });
  };
}