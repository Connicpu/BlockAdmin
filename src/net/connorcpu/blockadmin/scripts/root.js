function readFile(file) {
    var reader = new java.io.BufferedReader(new java.io.InputStreamReader(
        new java.io.FileInputStream(file), "UTF-8"));
}

function _s(str) {
    return "" + str;
}

var pages = {};

var moduleFolder = new java.io.File("./plugins/BlockAdmin/modules/");
var files = moduleFolder.listFiles();
for (var i = 0; i < files.length; ++i) {
    var file = files[i];
    var filename = file.name.toLowerCase();
    if (/\.mod$/i.test(filename)) {
        var data = JSON.parse(readFile(file));
        for (var pageName in data.pages) {
            if (pages[pageName]) {
                var page = pages[pageName];
                var dpage = data.pages[pageName];
                foreach (var tabName in dpage.tabs) {
                    if (page.tabs[tabName]) {
                        var tab = page.tabs[tabName];
                        var dtab = dpage.tabs[tabName];
                        foreach (var i in dtab.controls) {
                            tab.controls.push(dtab.controls[i]);
                        }
                    } else {
                        page.tabs[tabName] = data.pages[pageName].tabs[tabName];
                    }
                }
            } else {
                pages[pageName] = data.pages[pageName];
            }
        }
    }
}

var meta = {
    canReuse: function () {
        return true;
    }
}

var handler = {
    index: function (response, method, params, body) {
        response.writer.println(pages.toJSON());
    },
    data: function (response, method, params, body) {
        var basicinfo = {
            mainheader: _s(config.get("panelTitle")),
            server: {
                title: _s(config.get("serverTitle")),
                motd: _s(server.motd)
            }
        };
        response.writer.println(basicinfo.toJSON());
    }
};