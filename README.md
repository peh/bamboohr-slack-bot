# bamboohr-slack-bot
[![Build Status](https://travis-ci.org/peh/bamboohr-slack-bot.svg?branch=master)](https://travis-ci.org/peh/bamboohr-slack-bot)

A simple slack-bot that posts "Who's out" notifications.

## Requirements

* a small machine (e.g. t3.nano on ec2) 
* a slack developer account
* a slack app that supports bots and slack commands

## Usage

See [the docker repo](https://github.com/peh/bamboohr-slack-bot-deploy) to get information how to deploy the app with docker.

## Usage of the Bot

1. create an API key in bamboohr
1. `/bamboo login $api_key`
1. `/bamboo preview` to see that it's working
1. invite the bot to the channel he is supposed to post to
1. `/bamboo add #channel` to add the bot the a channel
1. ...
1. profit?
