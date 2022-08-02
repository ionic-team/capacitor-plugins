import XCTest
@testable import GoogleMapsPlugin

class GoogleMapsPluginTests: XCTestCase {
    var plugin: CapacitorGoogleMaps?

    override func setUpWithError() throws {
        self.plugin = CapacitorGoogleMaps()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func exampleTest() throws {
        do {
            if let plugin = self.plugin {
                let value = try plugin.echo("Hello world!")

                XCTAssertEqual("Hello world!", value)
            }
        } catch {
            print("Example Test Error \(error)")
            XCTFail()
        }
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
