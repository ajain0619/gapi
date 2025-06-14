# Swagger

## How to connect to the Swagger UI

Use the Swagger UI to manually test new API's.

* First make sure the server is up and running using the `make start` command
* Use the following URL to login in order to use the Swagger UI

```aidl
http://localhost:8080/geneva/login
```

* Once logged in you can access the swagger UI with the following URL:

```aidl
http://localhost:8080/geneva/swagger-ui.html
```
Geneva current swagger version is 3.0
Annotation were completely changed from 2.0 to 3.0

Here are the mappings from 2.0 to 3.0 , Those who are familiar with 2.0 version

Package for swagger 3 annotations is io.swagger.v3.oas.annotations.

@Api → @Tag

@ApiIgnore → @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden

@ApiImplicitParam → @Parameter

@ApiImplicitParams → @Parameters

@ApiModel → @Schema

@ApiModelProperty(hidden = true) → @Schema(accessMode = READ_ONLY)

@ApiModelProperty → @Schema

@ApiOperation(value = "foo", notes = "bar") → @Operation(summary = "foo", description = "bar")

@ApiParam → @Parameter

@ApiResponse(code = 404, message = "foo") → @ApiResponse(responseCode = "404", description = "foo")

Examples
Create summary of the request method handler by using @Operation
Expected response can be declared as follows
```
  @Operation(summary = "Create a notification entity.")
  @ApiResponse(content = @Content(schema = @Schema(implementation = AdNotificationDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AdNotificationDTO> createAdNotification(
      @RequestBody @Valid AdNotificationDTO adNotificationDTO) {

```
