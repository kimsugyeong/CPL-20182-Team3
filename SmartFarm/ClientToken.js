var token;

module.exports = {
    setToken: function (id) {
        token = id;
    },
    getToken: function () {
        return token;
    }
}
