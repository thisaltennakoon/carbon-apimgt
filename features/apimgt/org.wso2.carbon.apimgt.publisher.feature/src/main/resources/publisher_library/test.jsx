import 'react-app-polyfill/ie11';
import 'react-app-polyfill/stable';
import 'fastestsmallesttextencoderdecoder'; // Added to fix TextEncoding issue in edge <79

import ReactDOM from 'react-dom';
import React from 'react';
import Publisher from './dist_publisher_jsx_dev/publisher.js';

ReactDOM.render(<Publisher />, document.getElementById('react-root'));