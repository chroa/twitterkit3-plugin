
#import "TwitterConnect.h"
#import <TwitterKit/TwitterKit.h>

@implementation TwitterConnect

- (void)pluginInitialize
{
	NSString* consumerKey = [self.commandDelegate.settings objectForKey:[@"TwitterConsumerKey" lowercaseString]];
    NSString* consumerSecret = [self.commandDelegate.settings objectForKey:[@"TwitterConsumerSecret" lowercaseString]];
 	[[Twitter sharedInstance] startWithConsumerKey:consumerKey consumerSecret:consumerSecret];
}

- (void)login:(CDVInvokedUrlCommand*)command
{
  [[Twitter sharedInstance] logInWithCompletion:^(TWTRSession *session, NSError *error) {
	CDVPluginResult* pluginResult = nil;
	if (session){
		NSLog(@"signed in as %@", [session userName]);
		NSDictionary *userSession = @{
									  @"userName": [session userName],
									  @"userId": [session userID],
									  @"secret": [session authTokenSecret],
									  @"token" : [session authToken]};
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:userSession];
	} else {
		NSLog(@"error: %@", [error localizedDescription]);
		pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
	}
	[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
	}];
}

- (void)logout:(CDVInvokedUrlCommand*)command
{
	CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
	[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)showUser:(CDVInvokedUrlCommand*)command
{
  TWTRAPIClient *apiClient = [[TWTRAPIClient alloc] initWithUserID:[Twitter sharedInstance].sessionStore.session.userID];

  NSMutableDictionary *requestParameters = [[NSMutableDictionary alloc] init];
  [requestParameters setObject:[Twitter sharedInstance].sessionStore.session.userID forKey:@"user_id"];
  
  NSString *include_entities = @"false";
  
  if([[command.arguments objectAtIndex:0] objectForKey:@"include_entities"] != nil) {
      if([[[command.arguments objectAtIndex:0] objectForKey:@"include_entities"] boolValue] == YES) {
          include_entities = @"true";
      }
  }
  
  [requestParameters setObject:include_entities forKey:@"include_entities"];

	NSError *error = nil;
    
	NSURLRequest *apiRequest = [apiClient URLRequestWithMethod:@"GET"
														   URL:@"https://api.twitter.com/1.1/users/show.json"
                                                    parameters:requestParameters
														 error:&error];
	[apiClient sendTwitterRequest:apiRequest
					   completion:^(NSURLResponse *response, NSData *data, NSError *error) {
						   NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
						   NSInteger _httpStatus = [httpResponse statusCode];

						   CDVPluginResult *pluginResult = nil;
						   NSLog(@"API Response :%@",response);
						   if (error != nil) {
							   pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
						   } else if (_httpStatus == 200) {
							   NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
							   pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDict];
						   }
						   [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

					   }];
}

- (void)verifyCredentials:(CDVInvokedUrlCommand*)command
{
  TWTRAPIClient *apiClient = [[TWTRAPIClient alloc] initWithUserID:[Twitter sharedInstance].sessionStore.session.userID];
  
  NSMutableDictionary *requestParameters = [[NSMutableDictionary alloc] init];
  
  NSString *include_entities = @"false";
  NSString *skip_status = @"true";
  NSString *include_email = @"true";
  
  if([[command.arguments objectAtIndex:0] objectForKey:@"include_entities"] != nil) {
      if([[[command.arguments objectAtIndex:0] objectForKey:@"include_entities"] boolValue] == YES) {
          include_entities = @"true";
      }
  }
  if([[command.arguments objectAtIndex:0] objectForKey:@"skip_status"] != nil) {
      if([[[command.arguments objectAtIndex:0] objectForKey:@"skip_status"] boolValue] == NO) {
          skip_status = @"false";
      }
  }
  if([[command.arguments objectAtIndex:0] objectForKey:@"include_email"] != nil) {
      if([[[command.arguments objectAtIndex:0] objectForKey:@"include_email"] boolValue] == NO) {
          include_email = @"false";
      }
  }
  
  [requestParameters setObject:include_entities forKey:@"include_entities"];
  [requestParameters setObject:skip_status forKey:@"skip_status"];
  [requestParameters setObject:include_email forKey:@"include_email"];
  
  NSError *error = nil;
  
  NSURLRequest *apiRequest = [apiClient URLRequestWithMethod:@"GET"
                                                         URL:@"https://api.twitter.com/1.1/account/verify_credentials.json"
                                                  parameters:requestParameters
                                                       error:&error];
  [apiClient sendTwitterRequest:apiRequest
                     completion:^(NSURLResponse *response, NSData *data, NSError *error) {
                         NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
                         NSInteger _httpStatus = [httpResponse statusCode];
                         
                         CDVPluginResult *pluginResult = nil;
                         NSLog(@"API Response :%@",response);
                         if (error != nil) {
                             pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
                         } else if (_httpStatus == 200) {
                             NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
                             pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDict];
                         }
                         [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                         
                     }];
}

@end
