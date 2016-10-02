'use strict';

// react.js
const React = require('react');
const ReactDOM = require('react-dom');

// bootstrap
window.jQuery = require('jquery');
window.Tether = require('tether');
require('bootstrap');
require('bootstrap/dist/css/bootstrap.css');

require('font-awesome/css/font-awesome.css');

const client = require('./client.js');
require('../scss/app.scss');

class Plugin extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const plugin = this.props.plugin;

        var action = function (name) {
            return function () {
                client('rest/plugins/' + plugin.id + "/" + name).then(function (response) {
                    this.props.onStatusChanged(response.entity);
                }.bind(this));
            }.bind(this);
        }.bind(this);

        return (
            <tr>
                <td><input type="radio" value={plugin.id} checked={this.props.selected} onChange={this.props.onSelect}/></td>
                <td>{this.props.plugin.id}</td>
                <td>{this.props.plugin.name}</td>
                <td>{this.props.plugin.status}</td>
                <td>
                    <div className="btn btn-group" role="group">
                        <button className="btn btn-outline-danger btn-sm" onClick={action('initialize')}>
                            <i className="fa fa-eraser" aria-hidden="true"/>
                        </button>
                        <button className="btn btn-outline-warning btn-sm" onClick={action('stop')}>
                            <i className="fa fa-stop" aria-hidden="true"/>
                        </button>
                        <button className="btn btn-outline-success btn-sm" onClick={action('start')}>
                            <i className="fa fa-play" aria-hidden="true"/>
                        </button>
                        <button className="btn btn-outline-primary btn-sm" onClick={action('restart')}>
                            <i className="fa fa-refresh" aria-hidden="true"/>
                        </button>
                    </div>
                </td>
            </tr>
        );
    }

}

class PluginSettings extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            settings: {}
        };
    }

    componentWillReceiveProps(nextProps) {
        var plugin = nextProps.plugin;
        if (plugin == null) {
            return;
        }
        client('rest/plugins/' + plugin.id + '/settings').then(response=> {
            var settings = response.entity;
            this.setSettings(settings);
        });
    }

    save() {
        var plugin = this.props.plugin;
        client({
            path: 'rest/plugins/' + plugin.id + '/settings',
            method: 'PUT',
            entity: this.state.settings
        }).then(response=> {
            var settings = response.entity;
            this.setSettings(settings);
        })
    }

    setSettings(settings) {
        this.setState({
            settings: settings
        });
    }

    addNewSetting() {
        var settings = this.state.settings;
        var n = 0;
        var name = null;
        do {
            if (n == 0) {
                name = "Setting";
            } else {
                name = "Setting_" + n;
            }
            n++;
        } while (settings[name] != undefined);
        settings[name] = "Value";
        this.forceUpdate();
    }

    render() {
        var plugin = this.props.plugin;
        var settings = this.state.settings;
        var rows = [];
        var self = this;
        var names = [];
        for (var name in settings) {
            names.push(name);
        }
        names.sort().forEach(name=> {
            var value = settings[name];
            rows.push(<tr key={"key" + name}>
                <td className="col-md-4">
                    <input className="form-control" type="text" value={name} onChange={(function (name) {
                        return function (e) {
                            var value = self.state.settings[name];
                            delete self.state.settings[name];
                            self.state.settings[e.target.value] = value;
                            self.forceUpdate();
                        };
                    }(name))}/>
                </td>
                <td className="col-md-6">
                    <input className="form-control" type="text" value={value} onChange={(function (name) {
                        return function (e) {
                            self.state.settings[name] = e.target.value;
                            self.forceUpdate();
                        };
                    }(name))}/>
                </td>
                <td className="col-md-2">
                    <button className="btn btn-outline-warning btn-sm" onClick={(function (name) {
                        return function (e) {
                            delete self.state.settings[name];
                            self.forceUpdate();
                        };
                    }(name))}>
                        <i className="fa fa-trash" aria-hidden="true"/>
                    </button>
                </td>
            </tr>);
        });
        return (
            <div>
                <table className="table">
                    <thead>
                    <tr>
                        <th className="col-md-4">Name</th>
                        <th className="col-md-6">Value</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows}
                    </tbody>
                </table>
                <button className="btn btn-outline-info" onClick={this.addNewSetting.bind(this)}>
                    <i className="fa fa-plus" aria-hidden="true"/> Add
                </button>
                <span> </span>
                <button className="btn btn-outline-primary" disabled={this.props.plugin == null} onClick={this.save.bind(this)}>
                    <i className="fa fa-floppy-o" aria-hidden="true"/> Save
                </button>
            </div>
        )
    }
}

class PluginList extends React.Component {

    onSelect(e) {
        var _this = this;
        this.props.plugins.forEach(p => {
            if (p.id === e.currentTarget.value) {
                _this.props.onChange(p);
            }
        });
    }

    render() {
        return (
            <table className="table">
                <thead>
                <tr>
                    <th></th>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {this.props.plugins.map((item, index)=> {
                    return <Plugin key={index} plugin={item} selected={this.props.selectedPlugin.id === item.id}
                                   onSelect={this.onSelect.bind(this)}
                                   onStatusChanged={this.props.onStatusChanged}/>
                })}
                </tbody>
            </table>
        );
    }
}

class Root extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            plugins: [],
            selectedPlugin: null
        };
    }

    componentDidMount() {
        client('rest/plugins').then(response=> {
            var plugins = response.entity;
            var selectedPlugin = plugins.length == 0 ? null : plugins[0];
            this.setState({
                plugins: plugins,
                selectedPlugin: selectedPlugin
            });
        });
    }

    updatePluginStatus(plugin) {
        var plugins = this.state.plugins;
        for (var i in plugins) {
            var p = plugins[i];
            if (p.id === plugin.id) {
                plugins[i] = plugin;
                break;
            }
        }
        this.setState({
            plugins: plugins,
        });
    }

    selectedPluginChanged(selectedPlugin) {
        this.setState({
            selectedPlugin: selectedPlugin
        });
    }

    render() {
        return (
            <div className="container-fluid">
                <h1>Admin Console</h1>
                <div className="card card-block">
                    <h3 className="card-title">Plugin Management</h3>
                    <div className="col-sm-12">
                        <div className="col-sm-7">
                            <PluginList plugins={this.state.plugins}
                                        selectedPlugin={this.state.selectedPlugin}
                                        onChange={this.selectedPluginChanged.bind(this)}
                                        onStatusChanged={plugin=>this.updatePluginStatus(plugin)}/>
                        </div>
                        <div className="col-sm-5" style={{borderLeft: "1px solid gray"}}>
                            <PluginSettings plugin={this.state.selectedPlugin}/>
                        </div>
                    </div>
                    <div className="col-sm-12">
                        Upload Plugin:
                        <form action="rest/plugins" method="POST" encType="multipart/form-data">
                            <input id="file" name="file" type="file" style={{"display": "none"}} onChange={function (e) {
                                var fileName = e.target.value;
                                jQuery("#photoCover").val(fileName);
                            }}/>
                            <div className="input-group">
                                <input type="text" id="photoCover" className="form-control" placeholder="select file..."/>
                                <span className="input-group-btn">
                                <button type="button" className="btn btn-outline-info" onClick={function () {
                                    jQuery("#file").click()
                                }}>...</button>
                            </span>
                                <span className="input-group-btn">
                            <button className="btn btn-primary" onClick={function (e) {
                                console.log(e);
                            }}>Upload
                            </button>
                            </span>
                            </div>
                        </form>
                    </div>
                </div>
                <div className="card card-block">
                    <h3 className="card-title">System</h3>
                    <div className="btn-group" role="group">
                        <button className="btn btn-outline-danger" onClick={function () {
                            client('rest/system/restart').then(function (response) {
                                window.alert(response.entity.message);
                            });
                        }}>Restart
                        </button>
                        <button className="btn btn-outline-danger" onClick={function () {
                            client('rest/system/exit').then(function (response) {
                                window.alert(response.entity.message);
                            });
                        }}>Exit
                        </button>
                    </div>
                </div>
            </div>
        )
    }
}

ReactDOM.render(
    <Root />,
    document.getElementById('content')
);