# twitter-connect-plugin
### **Using Twitter Kit 3**
Cordova/PhoneGap plugin to use Twitter Single Sign On 

### Install

##### Create a Twitter app

Create a Twitter application in https://apps.twitter.com and get the consumer key and secret under the "Keys and Access Tokens" tab.

Make sure that the Callback URL is filled in when creating the application. Any valid url will do, Twitter recommends http://placeholder.com.

If desired to see the user's email (for example, by using the verify credentials endpoint), the "Additional Permissions" (in the "Permissions" tab) needs to be checked. Consequently, fill in the respective Privacy Policy URL and Terms of Service URL fields.

##### Add plugin to your Cordova app

Make sure you put in your valid API keys in their respective place.

`cordova plugin add https://github.com/chroa/twitter-connect-plugin#twitterkit3 --variable TWITTER_KEY=<Twitter Consumer Key> --variable TWITTER_SECRET=<Twitter Consumer Secret>`

### Usage

This plugin adds an object to the window named TwitterConnect. The following methods are provided by the plugin.

#### Login

Login using the `.login` method:
```
TwitterConnect.login(
  function(result) {
    console.log('[Login] - Successful login!');
    console.log(result);
  },
  function(error) {
    console.log('[Login] - Error logging in: ' + error);
  }
);
```

The login reponse object is defined as follows.
```
{
  userName: '<Twitter User Name>',
  userId: '<Twitter User Id>',
  secret: '<Twitter Oauth Secret>',
  token: '<Twitter Oauth Token>'
}
```

**/ ! \ For iOS, add the following code to your AppDelegate file**
//Thanks to [@donuzium](https://github.com/chroa/twitter-connect-plugin/issues/8)
```
#import "AppDelegate.h"
#import "MainViewController.h"
// added
#import <TwitterKit/TwitterKit.h>

@implementation AppDelegate
- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    self.viewController = [[MainViewController alloc] init];
    return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

// added
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options {
    return [[Twitter sharedInstance] application:app openURL:url options:options];
}

@end
```

#### Logout

Logout using the `.logout` method:
```
TwitterConnect.logout(
  function() {
    console.log('[Logout] - Successful logout!');
  },
  function() {
    console.log('[Logout] - Error logging out');
  }
);
```

#### ShowUser

Show a user's profile information. Returns a JSON object as specified in the API: https://developer.twitter.com/en/docs/accounts-and-users/follow-search-get-users/api-reference/get-users-show
```
TwitterConnect.showUser(

  /*Endpoint arguments*/
  {"include_entities" : false},

  /*Callback functions*/
  function(result) {
    console.log('[ShowUser] - Success!');
    console.log(result);
    console.log('[ShowUser] - Twitter handle: ' + result.screen_name);
  },
  function(error) {
    console.log('[ShowUser] - Error: ' + error);
  }
);
```


#### VerifyCredentials

Show's a user's profile information with added details as specified. Returns a JSON object as specified in the API: https://developer.twitter.com/en/docs/accounts-and-users/manage-account-settings/api-reference/get-account-verify_credentials
```
TwitterConnect.verifyCredentials(

  /*Endpoint arguments*/
  {"include_entities": false,
  "skip_status" : true,
  "include_email": true},

  /*Callback functions*/
  function(result) {
    console.log('[VerifyCredentials] - Success!');
    console.log(result);
  },
  function(error) {
    console.log('[VerifyCredentials] - Error: ' +  error);
  }
);
```
