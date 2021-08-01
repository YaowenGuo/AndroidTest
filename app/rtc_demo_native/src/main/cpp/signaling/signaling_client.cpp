//
// Created by Albert on 2021/7/28.
//

#include <string>
#include <utils/native_debug.h>
#include <iostream>
#include <condition_variable>

#include "signaling_client.h"

using std::string;

namespace rtc_demo {
    const string URL = "http://192.168.1.10:80";
    std::mutex _lock;
    std::condition_variable_any _cond;
    bool connect_finish = false;

    class connection_listener {
        sio::client &handler;

    public:

        connection_listener(sio::client &h) :
                handler(h) {
        }


        void on_connected() {
            _lock.lock();
            _cond.notify_all();
            connect_finish = true;
            _lock.unlock();
        }


        void on_close(sio::client::close_reason const &reason) {
            std::cout << "sio closed " << std::endl;
            exit(0);
        }


        void on_fail() {
            std::cout << "sio failed " << std::endl;
            exit(0);
        }
    };


    SignalingClient::SignalingClient(SocketCallbackInterface *callback) {
        callback_ = callback;
        connection_listener listener(client_);

        client_.set_open_listener(std::bind(&connection_listener::on_connected, &listener));
        client_.set_close_listener(
                std::bind(&connection_listener::on_close, &listener, std::placeholders::_1));
        client_.set_fail_listener(std::bind(&connection_listener::on_fail, &listener));
        client_.connect(URL);
        auto socket = client_.socket();
        socket->on("created", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    // 房间创建者收到此回调
                    LOGE("webrtc_albert: %s", "room created");
                    callback_->onCreateRoom();
                }));


        socket->on("joined", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    LOGE("webrtc_albert: %s", "room joined");
                    callback_->onJoinedRoom();
                }));

        socket->on("join", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    // 其他用户加入的时候收到此回调
                    LOGE("webrtc_albert: %s", "room join");
                    callback_->onPeerJoined();
                }));

        socket->on("full", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    LOGE("webrtc_albert: %s", "room full");
                }));

        socket->on("log", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    printMsgLog(string(), data);
                }));
        socket->on("bye", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    LOGE("webrtc_albert: %s", "room bye");
                    callback_->onPeerLeave(name);
                }));

        socket->on("message", sio::socket::event_listener_aux(
                [&](string const &name, message::ptr const &data, bool isAck,
                    message::list &ack_resp) {
                    if (data->get_flag() == sio::message::flag_object) {
                        auto type = data->get_map()["type"]->get_string();
                        if (type == "offer") {
                            callback_->onSDPReceived(data->get_string());
                        } else if ("answer" == type) {
                            callback_->onSDPReceived(data->get_string());
                        } else if ("candidate" == type) {
                            callback_->onIceCandidateReceived(data->get_string());
                        } else {
                            LOGE("webrtc_albert: message: %s", data->get_string().c_str());
                        }
                    } else {
                        LOGE("webrtc_albert: message: %s", "message $args");
                    }
                }));
    }


    SignalingClient::~SignalingClient() {
        if (callback_) {
            delete callback_;
        }
    }


    void SignalingClient::join(const string &name) {
        auto socket = client_.socket();
        socket->emit("create or join", name);
    }


    void SignalingClient::leave() {
        auto socket = client_.socket();
        socket->emit("bye");
    }


    void SignalingClient::SendSessionDescription(string const &sdp) {
        auto socket = client_.socket();
        socket->emit("message", sdp);
    }


    void SignalingClient::SendIceCandidate(string const &candidate) {
        auto socket = client_.socket();
        socket->emit("message", candidate);
    }


    void printMsgLog(string prefix, message::ptr const &data) {
        if (data == nullptr) return;
        LOGD("webrtc_albert:  log: ");
        switch (data->get_flag()) {
            case sio::message::flag_integer:
                LOGD("%s%lld", prefix.c_str(), data->get_int());
                std::cout << prefix << data->get_int() << std::endl;
                break;
            case sio::message::flag_double:
                LOGD("%s%lf", prefix.c_str(), data->get_double());
                break;
            case sio::message::flag_string:
                LOGD("%s%s", prefix.c_str(), data->get_string().c_str());
                break;
            case sio::message::flag_binary:
                LOGD("%s%s", prefix.c_str(), data->get_binary()->c_str());
                break;
            case sio::message::flag_array:
                for (auto &it: data->get_vector()) {
                    printMsgLog(prefix + "   ", it);
                }
                break;
            case sio::message::flag_object:
                LOGD("%s%s", prefix.c_str(), data->get_string().c_str());
                break;
            case sio::message::flag_boolean:
                LOGD("%s%d", prefix.c_str(), data->get_bool());
                break;
            case sio::message::flag_null:
                LOGD("%s%s", prefix.c_str(), ": null");
                break;
        }
    }
}