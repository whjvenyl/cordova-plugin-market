//
//  CDVMarket.h
//
// Created by Miguel Revetria miguel@xmartlabs.com on 2014-03-17.
// License Apache 2.0

#include "CDVMarket.h"

@implementation CDVMarket

- (void)pluginInitialize
{
}

- (void)open:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSArray *args = command.arguments;
        NSString *appId = [args objectAtIndex:0];

        NSLog(@"%@", args);

        CDVPluginResult *pluginResult;
        if (![self stringIsEmpty:appId]) {
            NSString *url = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/%@", appId];
            NSLog(@"%@", url);

            NSString *scheme = [args objectAtIndex:1];
            NSLog(@"%@", scheme);

            if (![self stringIsEmpty:scheme]) {
                NSLog(@"In condition");
                NSURL *urlFromScheme = [NSURL URLWithString:scheme];
                if ([[UIApplication sharedApplication] canOpenURL: urlFromScheme]) {
                    NSLog(@"Open from scheme");
                    [[UIApplication sharedApplication] openURL:urlFromScheme options:@{} completionHandler:nil];
                } else {
                    NSLog(@"Open with url 1");
                    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
                }
            } else {
                NSLog(@"Open with url 2");
                [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
            }
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid application id: null was found"];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (BOOL) stringIsEmpty:(NSString *) aString {

    if ((NSNull *) aString == [NSNull null]) {
        return YES;
    }

    if (aString == nil) {
        return YES;
    } else if ([aString length] == 0) {
        return YES;
    } else {
        aString = [aString stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([aString length] == 0) {
            return YES;
        }
    }

    return NO;
}

@end
