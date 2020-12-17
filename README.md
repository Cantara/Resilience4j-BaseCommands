![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cantara/Resilience4j-BaseCommands) [![Build Status](https://jenkins.entraos.io/buildStatus/icon?job=Resilience4j-BaseCommands)](https://jenkins.entraos.io/view/Build%20Monitor/job/Resilience4j-BaseCommands/) ![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Cantara/Resilience4j-BaseCommands/?foo=bar) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![Known Vulnerabilities](https://snyk.io/test/github/Cantara/Resilience4j-BaseCommands/badge.svg)](https://snyk.io/test/github/Cantara/Resilience4j-BaseCommands)

# Resilience4j-BaseCommands
Basic Commands for HTTP using CircuitBreaker with Resilience4j

 * BaseHttpGetResilience4jCommand
 * BaseHttpPostResilience4jCommand
 * BaseHttpPutResilience4jCommand
 * BaseResilience4jCommand

## Usage

```
CommandProxy commandProxy = new CommandProxy();
URI baseUri = URI.create("https://example.com/");
BaseResilience4jCommand command = new BaseHttpGetResilience4jCommand(baseUri, "name-of-similar-commands");
HttpResponse<String> response = commandProxy.run(command);
int status = response.statusCode();
String body = response.body();
```
 
 

 
