//
//  UIUserInterfaceIdiom+Extensions.swift
//  Plugin
//
//  Created by Kondrat Kielar on 19/01/2022.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

import Foundation
import UIKit

extension UIUserInterfaceIdiom {
    
    var bridgeValue: String {
        
        switch self {
        case .carPlay:
            return "car"
        case .mac:
            return "desktop"
        case .pad:
            return "tablet"
        case .phone:
            return "smartphone"
        case .tv:
            return "tv"
        case .unspecified:
            return "unknown"
        @unknown default:
            return "unknown"
        }
    }
}
