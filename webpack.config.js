const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: "./src/main/typescript/index.tsx",
  output: {
    filename: "bundle.js",
    path: __dirname + "/target/scala-2.12/classes/webroot"
  },

  devtool: "source-map",

  resolve: {
    extensions: [".ts", ".tsx", ".js", ".json"]
  },

  module: {
    rules: [
      { test: /\.tsx?$/, loader: "awesome-typescript-loader" },
      { enforce: "pre", test: /\.js$/, loader: "source-map-loader" },
      { test: /\.css$/, use: [
          { loader: "style-loader/url" },
          { loader: "file-loader" }
      ]}
    ],
  },

  plugins: [
    new HtmlWebpackPlugin({
      "template": "src/main/typescript/index.html"
    })
  ]
};
