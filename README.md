# HTTP Live Streaming Validator

A Java-based application for validating HTTP Live Streaming protocol files.

## Purpose

##### HTTP Live Streaming
HTTP Live Streaming (HLS) is a protocol for HTTP-based media streaming communications.  This works by 
breaking up a full stream of data into smal ler, sequenced chunks to be consumed over HTTP.  Each "chunked"
download provides a part of a potentially unbounded transport stream.  The specification was created by
Apple and support for HLS specs are required by Apple for any iTunes store application.

##### HLS Specification (RFC) v.7
The HLS specification currently in Request For Comments state
  >       https://tools.ietf.org/html/rfc8216

## Usage

##### Notes
- Currently this project only supports a small set of protocol validations :)
- The program allows for validating HLS protocol files over HTTP as well as on local filesystem.

##### Compilation
Compile with Java SDK 8
  >       javac HTTPLiveStreamingValidator.java

##### Runtime
- Run with Java 8 RTE
- Can operate in an interactive mode from CLI when no arguments provided:
  >       java HTTPLiveStreamingValidator
- Can operate in an batch mode and validate a list of HLS files when given path to a text file
as an argument.  This text file should contain a list of URLs to playlist files:
  >       java HTTPLiveStreamingValidator "[some random path]/[another folder]/listOfPlaylistURLs.txt"

