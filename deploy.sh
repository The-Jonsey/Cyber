#!/usr/bin/env bash
curl -d "word=$1" -H "Content-Type: application/x-www-form-urlencoded" -X POST https://deploy.thejonsey.com/