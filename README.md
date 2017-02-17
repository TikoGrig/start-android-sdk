## Start Mobile SDK [![Build Status](https://api.travis-ci.org/payfort/start-android-sdk.svg?branch=master)](https://travis-ci.org/payfort/start-android-sdk/) [ ![Download](https://api.bintray.com/packages/pavel-gabriel/payfort/start/images/download.svg) ](https://bintray.com/pavel-gabriel/payfort/start/_latestVersion)
Start is a payment gateway for Startups

## Intro
Due to security rules from Visa/MasterCard developers can’t transmit card details directly to their server and have to use PCI DSS certified environment for this. Instead, card details are sent directly to our server and we return a card token in the response. The card token uniquely identifies each card, and can be used in all API calls that require a card e.g. create charge or customer.

Sometimes, during the payment process an additional 3D Secure verification may be required. This adds more complexity to payment flow. With mobile SDK we hide this complexity from developer.

## Installation
Before you start you have to sign up for a Start account [here](https://dashboard.start.payfort.com/#/public/sign_up?secretLink=true). This [instruction](https://docs.start.payfort.com/guides/api_keys/#how-to-get-api-keys) tells how to get API keys for SDK.

Download [the latest AAR](https://bintray.com/pavel-gabriel/payfort/start/_latestVersion) or grab via Maven:
```xml
<dependency>
  <groupId>com.payfort</groupId>
  <artifactId>start</artifactId>
  <version>LATEST_VERSION</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.payfort:start:LATEST_VERSION'
```
See the latest version [here](https://github.com/payfort/start-android-sdk/releases).

## Usage
SDK provides following classes:
+ `Card`: let developer validate card details and show errors on payment form
+ `Token`: representation of token received from API
+ `Start`: let developer create card token

#### Card
Use code below to create and validate new Card object:
```java
try {
    Card card = new Card(number, cvc, expirationMonth, expirationYear, owner);
} catch (CardVerificationException e) {
    // show validation errors
}
```
See `sample` app and javadoc for more details.

#### Start
The main goal of `Start` class is to create card token.
```java
Start start = new Start(API_KEY);
start.createToken(activity, card, callback, amount, currency);
```
This [instruction](https://docs.start.payfort.com/guides/api_keys/#how-to-get-api-keys) tells how to get API_KEY for SDK.

#### TokenCallback
Instance of `TokenCallback` is a callback you must provide to handle responses from Payfort Start. `TokenCallback` provides 3 methods:
+ `onSuccess` receives created token as argument
+ `onError` receives error object as argument with error from API
+ `onCancel` is called when user cancel receiving token

## Licence
The MIT License

Copyright (c) 2016-2017 Payfort ([payfort.com](http://www.payfort.com/))

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
