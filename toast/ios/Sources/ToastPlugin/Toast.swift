import Foundation
import UIKit

@objc public class Toast: NSObject {

    @objc public static func showToast(in viewController: UIViewController, text: String, duration: Int = 2000, position: String = "bottom", completion: ((Bool) -> Void)? = nil) {
        DispatchQueue.main.async {
            let maxSizeTitle: CGSize = CGSize(width: viewController.view.bounds.size.width-32, height: viewController.view.bounds.size.height)

            let label = ToastLabel()
            label.backgroundColor = UIColor.black.withAlphaComponent(0.6)
            label.textColor = UIColor.white
            label.textAlignment = .center
            label.text = text
            label.alpha = 0
            label.layer.cornerRadius = 18
            label.clipsToBounds  =  true
            label.lineBreakMode = .byWordWrapping
            label.numberOfLines = 0

            var expectedSizeTitle: CGSize = label.sizeThatFits(maxSizeTitle)
            // UILabel can return a size larger than the max size when the number of lines is 1
            let minWidth = min(maxSizeTitle.width, expectedSizeTitle.width)
            let minHeight = min(maxSizeTitle.height, expectedSizeTitle.height)
            expectedSizeTitle = CGSize(width: minWidth, height: minHeight)

            let height = expectedSizeTitle.height+32
            let yCoordinate: CGFloat
            if position == "top" {
                yCoordinate = 40
            } else if position == "center" {
                yCoordinate = (viewController.view.bounds.size.height/2) - (height/2)
            } else {
                yCoordinate = viewController.view.bounds.size.height - height - (height/2)
            }

            label.frame = CGRect(
                x: ((viewController.view.bounds.size.width)/2) - ((expectedSizeTitle.width+32)/2),
                y: yCoordinate,
                width: expectedSizeTitle.width+32,
                height: height)

            label.padding = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)

            viewController.view.addSubview(label)

            UIView.animateKeyframes(withDuration: 0.3, delay: 0, animations: {
                label.alpha = 1.0
            }, completion: {(isCompleted) in

                UIView.animate(withDuration: 0.3, delay: (Double(duration) / 1000), options: .curveEaseOut, animations: {
                    label.alpha = 0.0
                }, completion: {(isCompleted) in
                    label.removeFromSuperview()

                    if (completion) != nil {
                        completion?(isCompleted)
                    }
                })
            })
        }
    }
}
