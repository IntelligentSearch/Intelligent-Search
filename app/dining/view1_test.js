'use strict';

describe('myApp.dining module', function() {

  beforeEach(module('myApp.dining'));

  describe('dining controller', function(){

    it('should ....', inject(function($controller) {
      //spec body
      var view1Ctrl = $controller('DiningCtrl');
      expect(view1Ctrl).toBeDefined();
    }));

  });
});