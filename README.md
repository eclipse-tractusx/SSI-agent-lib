<a name="readme-top"></a>

<!-- Shields -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![APACHE-2.0 License][license-shield]][license-url]



<!-- Caption -->

<br />
<div align="center">
  <a href="https://eclipse-tractusx.github.io/img/logo_tractus-x.svg">
    <img src="https://eclipse-tractusx.github.io/img/logo_tractus-x.svg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Tractus-X SSI Library</h3>

  <p align="center">
    This open source project has emerged with a goal to foster collaborative development and innovation in the area of Self-Sovereign Identity.
    <br />
        <a href="https://github.com/catenax-ng/product-lab-ssi/tree/main/cx-ssi-lib/docs"><strong>Explore the docs »</strong></a>
        <br />
    <br />
    <a href="https://github.com/catenax-ng/product-lab-ssi/issues">Report Bug</a>
    ·
    <a href="https://github.com/catenax-ng/product-lab-ssi/issues">Request Feature</a>
  </p>
</div>




<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#key-features-and-objectives">Key Features and Objectives</a>
    </li>
    <li><a href="#benefits-and-impact">Benefits and Impact</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About The Project

This projects facilitates the implementation of Self-Sovereign Identity solution. Self-Sovereign Identity refers to a
decentralized model where individuals have control over their digital identities and can manage their personal data
securely and autonomously. It is developed within the [Tractus-X project](https://eclipse-tractusx.github.io/) under the umbrella of the Eclipse Foundation,

The Eclipse Foundation provides a neutral environment and governance structure for various
projects, enabling individuals and organizations to contribute and build upon each other's work.
Key Features and Objectives:

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- KEY FEATURES AND OBJECTIVES -->
# Key Features and Objectives

1. **Identity Management**: The project focuses on developing robust and flexible identity management capabilities, enabling individuals to maintain control over their digital identities, establish trust relationships, and selectively disclose personal information.

2. **Privacy and Security**: With privacy as a central principle, the project emphasizes implementing strong security measures and cryptographic protocols to safeguard sensitive data. It provides encryption, digital signatures, and other mechanisms to ensure data integrity and confidentiality.

3. **Community-Driven Development**: Emphasizing collaboration and inclusivity, the project encourages a vibrant and diverse community of developers, contributors, and users. It fosters active engagement through mailing lists, forums, and regular meetups, ensuring continuous improvement and knowledge sharing.

4. **Standards Compliance**: The project adheres to established industry standards, ensuring compatibility and interoperability with existing technologies. This facilitates seamless integration with other software systems and promotes widespread adoption.

5. **Extensibility**: The project offers extensibility options, allowing developers to customize and extend its functionalities to meet specific project requirements. This flexibility enables developers to adapt the software to their unique needs.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- BENEFITS AND IMPACT -->
# Benefits and Impact

By operating under the Eclipse Foundation, the project benefits from a robust ecosystem and collaborative environment. Some notable impacts and benefits of the project include:

1. **Accelerated Development**: The project's tools and frameworks enable developers to streamline their workflows, resulting in faster development cycles and increased productivity.

2. **Community Collaboration**: The open and inclusive nature of the project fosters collaboration, knowledge sharing, and innovation among developers and contributors, leading to continuous improvement and growth.

3. **Cost Savings**: As an open source project, the software is freely available, reducing licensing costs and enabling organizations to allocate resources towards other critical areas.

4. **Quality Assurance**: The project benefits from community involvement in testing, bug fixing, and security auditing, resulting in more reliable and secure software.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- INSTALLATION -->
## Installation

To install and use this lib , you have two options: building from source or using the pre-built package from Maven Central Repository.

### Build from Source

To build the project from source, follow these steps:

1. Clone the repository: 
   ```
   git clone https://github.com/catenax-ng/product-lab-ssi.git 
   ```

2. Navigate to the project directory:
   ```
   cd cx-ssi-lib 
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. After a successful build, you can include the generated JAR file in your project's dependencies.

### Use Maven Dependency

Alternatively, you can use the pre-built package available on Maven Central Repository by adding the following Maven dependency to your project's `pom.xml` file:

```xml
<dependency>
  <groupId>org.eclipse.tractusx.ssi</groupId>
  <artifactId>cx-ssi-agent-lib</artifactId>
  <version>0.0.3</version>
</dependency>
```

Make sure to update the version number if a newer version is available.

Once you've added the dependency, your build tool (e.g., Maven or Gradle) will automatically download the library and include it in your project.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- ROADMAP -->
# Roadmap

Our roadmap for future development includes the following key milestones:

1. **Enhanced DID Management**: We plan to further enhance the DID management functionality by adding support for additional DID methods and advanced features such as Using new methods for DID resolution and DID document updates.

2. **Extended Cryptographic Operations**: We aim to expand the cryptographic operations offered by the library, including support for more signing algorithms, advanced key management features.

3. **Credential Ecosystem**: We will focus on improving the credential management capabilities, including support for different credential formats, revocation mechanisms, and interoperability with existing credential ecosystems such as Verifiable Credentials.

4. **Standard Compliance**: We are committed to ensuring compliance with emerging SSI-related standards and specifications. We will continuously update the project to align with evolving standards and ensure compatibility with other SSI tools and platforms.

5. **Performance Optimization**: We will invest in optimizing the performance of the library, aiming to reduce computational overhead, improve efficiency, and provide better scalability for large-scale SSI agent deployments.

6. **Comprehensive Documentation and Tutorials**: We will continue to improve the project's documentation, providing comprehensive guides, examples, and tutorials to assist developers in effectively utilizing the library's features and integrating it into their SSI agent projects.

Please note that the roadmap is subject to change based on community feedback, emerging standards, and the evolving needs of the SSI ecosystem. We welcome contributions and suggestions from the community to help shape the future direction of the project.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing

We are thrilled to have you here and excited about your interest in contributing to our project. Your contributions play a vital role in making our project successful and we truly appreciate your support.

To ensure a smooth and enjoyable experience for everyone involved, we have put together this guide to help you understand how you can contribute effectively. Please take a moment to read through the [CONTRIBUTING.md](CONTRIBUTING.md) before you start contributing.

Please ensure that you adhere to the project's coding style, write unit tests for your changes if applicable, and provide clear documentation for any new features or changes.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
# License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for more information.

```
Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/
```

You can freely use, modify, and distribute this project under the terms of the Apache License 2.0.


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
# Contact

If you have any questions, suggestions, or feedback regarding this project, please feel free to reach out to us. You can contact our team at:

- Email: TBD! 
- Issue Tracker:TBD! 

We value your input and appreciate your interest in contributing to the project. Don't hesitate to contact us if you need any assistance or want to get involved.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


[contributors-shield]: https://img.shields.io/github/contributors/catenax-ng/product-lab-ssi.svg?style=for-the-badge
[contributors-url]: https://github.com/catenax-ng/product-lab-ssi/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/catenax-ng/product-lab-ssi.svg?style=for-the-badge
[forks-url]: https://github.com/catenax-ng/product-lab-ssi/network/members
[stars-shield]: https://img.shields.io/github/stars/catenax-ng/product-lab-ssi.svg?style=for-the-badge
[stars-url]: https://github.com/catenax-ng/product-lab-ssi/stargazers
[issues-shield]: https://img.shields.io/github/issues/catenax-ng/product-lab-ssi.svg?style=for-the-badge
[issues-url]: https://github.com/catenax-ng/product-lab-ssi/issues
[license-shield]: https://img.shields.io/github/license/catenax-ng/product-lab-ssi.svg?style=for-the-badge
[license-url]: https://github.com/catenax-ng/product-lab-ssi/blob/master/LICENSE.txt
