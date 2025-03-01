require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-netinfo"
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.platforms    = { :ios => "9.0", :tvos => "9.2", :osx => "10.14" }

  s.source       = { :git => "https://github.com/react-native-community/react-native-netinfo.git", :tag => "v#{s.version}" }
  s.source_files  = "ios/*.{h,m,c}"

  s.dependency 'React-Core'
end
