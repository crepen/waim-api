package com.waim.api.common.scalar_ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller

public class ScalarUIController {

    @GetMapping("/public/scalar-ui")
    @ResponseBody
    @Operation(hidden = true)
    @Profile({"local"})
    public String scalarApiDoc() {
        return """
            <!doctype html>
            <html>
              <head>
                <title>WAIM API Reference</title>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
              </head>
              <body>
                <script
                  id="api-reference"
                  data-url="/api/public/v3/api-docs"
                  data-configuration='{
                    "theme": "deepSpace",
                    "layout": "modern",
                    "showTestRequest": true,
                    "defaultHttpClient": {
                      "targetKey": "shell",
                      "clientKey": "curl"
                    },
                    "spec": { "preserveDiscriminator": true }
                  }'>
                </script>
                <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
              </body>
            </html>
            """;
    }
}
