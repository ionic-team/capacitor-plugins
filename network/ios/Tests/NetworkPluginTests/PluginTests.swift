import XCTest
@testable import CAPNetworkPlugin

class NetworkTests: XCTestCase {

    func testStatus() {
        do {
            let implementation = try Network()
            XCTAssertEqual(Network.Connection.wifi, implementation.currentStatus())
        } catch let error {
            XCTFail("Network initialization failed! \(error)")
        }
    }

    func testCallback() {
        do {
            let implementation = try Network()
            XCTAssertNotNil(implementation.reachability)

            let expectation = self.expectation(description: "Network will call the status observer")
            implementation.statusObserver = { status in
                XCTAssertEqual(Network.Connection.wifi, status)
                expectation.fulfill()
            }
            // swiftlint:disable:next force_unwrapping
            implementation.reachability?.whenReachable?(implementation.reachability!)

            waitForExpectations(timeout: 1) { error in
                if let error = error {
                    XCTFail("waitForExpectations errored: \(error)")
                }
            }
        } catch let error {
            XCTFail("Network initialization failed! \(error)")
        }
    }
}
