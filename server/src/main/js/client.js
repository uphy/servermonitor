var rest, mime;

rest = require('rest');
mime = require('rest/interceptor/mime');

module.exports = rest.wrap(mime, {
    mime: 'application/json'
});