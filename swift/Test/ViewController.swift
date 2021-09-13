//
//  ViewController.swift
//  Test
//
//  Created by Azamat on 01/09/21.
//

import UIKit

import Flutter
import FlutterPluginRegistrant

class ViewController: UIViewController {

    @IBOutlet weak var resultLabel: UILabel!
<<<<<<< HEAD
    @IBOutlet weak var scanMode: UISwitch!
    
    override func viewDidLoad() {
        super.viewDidLoad()
=======
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
>>>>>>> 29a79f30b01b69dc36bada275c5c824beb829187
    }


    @IBAction func faceScannerButton(_ sender: Any) {
        self.loadEngine()
    }
    
    func loadEngine() {
        var uri = URLComponents()
            uri.path = "/login"
            uri.queryItems = [
                URLQueryItem(name: "client_id", value: __CLIENT_ID__),
                URLQueryItem(name: "redirect_uri", value: __REDIRECT_URL__),
                URLQueryItem(name: "scope", value: "address,contacts,doc_data,common_data"),
                URLQueryItem(name: "language", value: "uz"),
<<<<<<< HEAD
                URLQueryItem(name: "scan_mode", value: self.scanMode.isOn ? "strong" : "simple;"),
=======
>>>>>>> 29a79f30b01b69dc36bada275c5c824beb829187
//                URLQueryItem(name: "passport", value: "AA1234567"),
//                URLQueryItem(name: "birthday", value: "01.01.2000"),
//                URLQueryItem(name: "user_hash", value: "1234567891234567"),
            ]
        
        let engine = FlutterEngine(name: "MyID engine")
        engine.run(withEntrypoint: "", initialRoute: uri.url?.absoluteString);
        GeneratedPluginRegistrant.register(with: engine);

        let viewController = FlutterViewController(engine: engine, nibName: nil, bundle: nil)
        present(viewController, animated: true, completion: nil)
        
        let channel = FlutterMethodChannel(name: "channel/myid", binaryMessenger: viewController.binaryMessenger)
        channel.setMethodCallHandler({
          (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            if (call.method == "result") {
                viewController.popRoute();
                
                self.resultLabel.text = "Result: \(String(describing: call.arguments))";
            }
        })
    }
}

