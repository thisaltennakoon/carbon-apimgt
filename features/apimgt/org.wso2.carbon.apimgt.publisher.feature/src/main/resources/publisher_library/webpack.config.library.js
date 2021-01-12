const path = require('path');
const fs = require('fs');

const config = {
    entry: {
        index: './source/src/library/library.js'
    },
    output: {
        path: path.resolve(__dirname, "dist_publisher_jsx_dev"),// dist_index_jsx
        filename: 'publisher.js',
        library: 'publisher', //library can be exported using this name
        libraryTarget: "this",// this, window, umd are also possible
    },
    resolve: {
        alias: {
            AppData: path.resolve(__dirname, 'source/src/app/data/'),
            AppComponents: path.resolve(__dirname, 'source/src/app/components/'),
            OverrideData: path.resolve(__dirname, 'override/src/app/data/'),
            OverrideComponents: path.resolve(__dirname, 'override/src/app/components/'),
            AppTests: path.resolve(__dirname, 'source/Tests/'),
            react: fs.existsSync('../../../../../node_modules/react')
                ? path.resolve('../../../../../node_modules/react') : path.resolve('../node_modules/react'),
            reactDom: fs.existsSync('../../../../../node_modules/react-dom')
                ? path.resolve('../../../../../node_modules/react-dom') : path.resolve('../node_modules/react-dom'),
        },
        extensions: ['.js', '.jsx'],
    },
    module: {
        rules: [
            {
                test: /\.worker\.js$/,
                use: { loader: 'worker-loader' },
            },
            {
                test: /\.(js|jsx)$/,
                exclude: [/node_modules\/(?!(@hapi)\/).*/, /coverage/],
                use: [
                    {
                        loader: 'babel-loader',
                    },
                    {
                        loader: path.resolve('loader.js'),
                    },
                ],
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.less$/,
                use: [
                    {
                        loader: 'style-loader', // creates style nodes from JS strings
                    },
                    {
                        loader: 'css-loader', // translates CSS into CommonJS
                    },
                    {
                        loader: 'less-loader', // compiles Less to CSS
                    },
                ],
            },
            {
                test: /\.(woff|woff2|eot|ttf|svg)$/,
                loader: 'url-loader?limit=100000',
            },
        ],
    },
    externals: {
        "userCustomThemes":"userThemes", // Should use long names for preventing global scope JS variable conflicts
        "MaterialIcons":"MaterialIcons",
        "Config":"AppConfig",
        "Settings":"Settings",
        "react":"react",
        "@babel/core":"@babel/core",
        "@babel/plugin-proposal-class-properties":"@babel/plugin-proposal-class-properties",
        "@babel/plugin-proposal-object-rest-spread":"@babel/plugin-proposal-object-rest-spread",
        "@babel/plugin-syntax-dynamic-import":"@babel/plugin-syntax-dynamic-import",
        "@babel/plugin-transform-spread":"@babel/plugin-transform-spread",
        "@babel/preset-env":"@babel/preset-env",
        "@babel/preset-react":"@babel/preset-react",
        "@babel/register":"@babel/register",
        "@stoplight/prism-http":"@stoplight/prism-http",
        "babel-eslint":"babel-eslint",
        "babel-jest":"babel-jest",
        "babel-loader":"babel-loader",
        "babel-plugin-dynamic-import-node":"babel-plugin-dynamic-import-node",
        "cross-env":"cross-env",
        "css-loader":"css-loader",
        "enzyme":"enzyme",
        "enzyme-adapter-react-16":"enzyme-adapter-react-16",
        "eslint":"eslint",
        "eslint-config-airbnb":"eslint-config-airbnb",
        "eslint-loader":"eslint-loader",
        "eslint-plugin-import":"eslint-plugin-import",
        "eslint-plugin-jest":"eslint-plugin-jest",
        "eslint-plugin-jsx-a11y":"eslint-plugin-jsx-a11y",
        "eslint-plugin-prettier":"eslint-plugin-prettier",
        "eslint-plugin-react":"eslint-plugin-react",
        "eslint-plugin-react-hooks":"eslint-plugin-react-hooks",
        "extract-react-intl-messages-compact":"extract-react-intl-messages-compact",
        "jest":"jest",
        "less":"less",
        "less-loader":"less-loader",
        "mock-local-storage":"mock-local-storage",
        "monaco-editor-webpack-plugin":"monaco-editor-webpack-plugin",
        "prettier":"prettier",
        "rimraf":"rimraf",
        "style-loader":"style-loader",
        "swagger-parser":"swagger-parser",
        "webpack":"webpack",
        "webpack-bundle-analyzer":"webpack-bundle-analyzer",
        "webpack-cli":"webpack-cli",
        "webpack-manifest-plugin":"webpack-manifest-plugin",
        "worker-loader":"worker-loader",
        "@hapi/hapi":"@hapi/hapi",
        "@material-ui/core":"@material-ui/core",
        "@material-ui/icons":"@material-ui/icons",
        "async-mutex":"async-mutex",
        "async-react-component":"async-react-component",
        "autosuggest-highlight":"autosuggest-highlight",
        "base64url":"base64url",
        "caniuse-api":"caniuse-api",
        "clean-webpack-plugin":"clean-webpack-plugin",
        "downshift":"downshift",
        "draft-js":"draft-js",
        "draftjs-to-html":"draftjs-to-html",
        "fastestsmallesttextencoderdecoder":"fastestsmallesttextencoderdecoder",
        "file-loader":"file-loader",
        "html-to-draftjs":"html-to-draftjs",
        "js-yaml":"js-yaml",
        "lodash.clonedeep":"lodash.clonedeep",
        "lodash.isempty":"lodash.isempty",
        "material-ui-chip-input":"material-ui-chip-input",
        "moment":"moment",
        "mui-datatables":"mui-datatables",
        "prop-types":"prop-types",
        "qs":"qs",
        "query-string":"query-string",
        "rc-notification":"rc-notification",
        "rc-progress":"rc-progress",
        "react-app-polyfill":"react-app-polyfill",
        "react-autosuggest":"react-autosuggest",
        "react-circular-progressbar":"react-circular-progressbar",
        "react-color":"react-color",
        "react-draft-wysiwyg":"react-draft-wysiwyg",
        "react-dropzone":"react-dropzone",
        "react-intl":"react-intl",
        "react-markdown":"react-markdown",
        "react-modal":"react-modal",
        "react-monaco-editor":"react-monaco-editor",
        "react-quill":"react-quill",
        "react-router":"react-router",
        "react-router-dom":"react-router-dom",
        "react-safe-html":"react-safe-html",
        "react-select":"react-select",
        "react-tagsinput":"react-tagsinput",
        "react-tap-event-plugin":"react-tap-event-plugin",
        "react-toastify":"react-toastify",
        "swagger-client":"swagger-client",
        "swagger-parser":"swagger-parser",
        "swagger-ui":"swagger-ui",
        "swagger-ui-react":"swagger-ui-react",
        "typeface-roboto":"typeface-roboto",
        "url-loader":"url-loader",
    },
};
module.exports = function (env) {
    if (env && env.analysis) {
        const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer');
        config.plugins.push(new BundleAnalyzerPlugin());
    }
    return config;
};
