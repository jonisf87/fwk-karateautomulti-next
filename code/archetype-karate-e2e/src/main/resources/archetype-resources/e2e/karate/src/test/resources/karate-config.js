function fn() {
  var env = karate.env || 'local';
  var APP_PORT = java.lang.System.getenv('APP_PORT') || '8080';

  var config = {};
  config.env = env;
  config.urls = {};

  if (env === 'local') {
    config.urls.demoApplicationUrl = 'http://localhost:' + APP_PORT;
  } else if (env === 'pre') {
    config.urls.demoApplicationUrl = 'https://dummyjson.com';
  } else {
    config.urls.demoApplicationUrl = 'http://localhost:' + APP_PORT;
  }

  return config;
}
