import Capacitor

extension CAPBridgeViewController {

    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        NotificationCenter.default.post(Notification(name: .capacitorViewDidAppear))
    }

    override open func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        NotificationCenter.default.post(Notification(name: .capacitorViewWillTransition))
    }
}
