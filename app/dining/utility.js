function getAPIURL() {
    return "http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest/";
}

//function getCookie(cname) {
//    var name = cname + "=";
//    var ca = document.cookie.split(';');
//    for(var i = 0; i <ca.length; i++) {
//        var c = ca[i];
//        while (c.charAt(0)==' ') {
//            c = c.substring(1);
//        }
//        if (c.indexOf(name) == 0) {
//            return c.substring(name.length,c.length);
//        }
//    }
//    return {};
//}
//
//function getUserObj() {
//    var response = getCookie('user');
//    if (response != undefined && response.data != undefined) {
//        var userID = response.data.user.UserID;
//        console.log("GetUserId:" + userID);
//        return userID;
//    } else {
//        return response;
//    }
//}