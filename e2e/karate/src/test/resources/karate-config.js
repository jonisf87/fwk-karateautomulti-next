function fn() {
  var env = karate.env || 'pre';
  var environmentConfig = karate.read('classpath:config-' + env + '.yml');
  var config = {
    env: env,
    urls: environmentConfig.urls || {},
    timeouts: environmentConfig.timeouts || {
      connect: 5000,
      read: 10000
    }
  };

  if (env === 'local') {
    var appPort = java.lang.System.getProperty('APP_PORT') || java.lang.System.getenv('APP_PORT');
    if (appPort) {
      config.urls.demoApplicationUrl = 'http://localhost:' + appPort + '/products';
    }
  }

  karate.configure('connectTimeout', config.timeouts.connect);
  karate.configure('readTimeout', config.timeouts.read);

  return config;
}
