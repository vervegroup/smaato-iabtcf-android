def gradlew(command) {
    withEnv([
            "ANDROID_HOME=${env.ANDROID_HOME}"
    ]) {
        sh "chmod +x gradlew && ./gradlew -Dorg.gradle.java.home=/usr/lib/jvm/java-11-amazon-corretto ${command}"
    }
}

def archive(module){
    archiveArtifacts artifacts: "${module}/build/outputs/aar/*.aar", fingerprint: true

    sh "mv ${module}/build/outputs/mapping/release/mapping.txt ${module}/build/outputs/mapping/release/${module}-mapping.txt"
    archiveArtifacts artifacts: "${module}/build/outputs/mapping/release/${module}-mapping.txt", fingerprint: true
}

def runCheckstyle() {
    echo 'Running Checkstyle analysis'
    script {
        try {
            gradlew 'checkstyle'
        } catch (Exception e) {
            echo 'Checkstyle violations found'
        }
    }
}

def runLint() {
    echo 'Running Lint analysis'
    script {
        try {
            gradlew 'lintRelease'
        } catch (Exception e) {
            echo 'Lint violations found'
        }
    }
}

def uninstallITApplication() {
    script {
        try {
            sh 'integration-tests/ci/uninstall.sh'
        } catch (Exception ignored) {
            echo 'Something went wrong during application uninstallation'
        }
    }
}

def getBranchName() {
    def branchName = "${env.BRANCH_NAME}"
    if (branchName.contains('decoder_release/')) {
        return branchName.replace('decoder_release/', '')
    } else {
        return branchName
    }
}

pipeline {
    environment {
        BUILD_BRANCH_NAME = getBranchName()
    }

    agent {
        label 'android-slave'
    }

    triggers {
        pollSCM('')
    }

    stages {
        stage('Static analysis') {
            when {
                not { branch 'develop' }
            }
            steps {
                sh "java -version"
                sh "javac -version"

                gradlew 'clean'
                withEnv(["LINT_PRINT_STACKTRACE=true"]) {
                    runCheckstyle()
                    runLint()
                }
            }
            post {
                always {
                    checkStyle reportEncoding: 'UTF-8', pattern: '**/checkstyle-report.xml'
                    androidLintParser reportEncoding: 'UTF-8', pattern: '**/lint-report.xml'
                }
            }
        }

        stage('Assemble TCString decoder module') {
            when {
                anyOf {
                    branch 'decoder_release/**'
                    branch 'develop'
                }
            }
            steps {
                echo 'Building Smaato Iab decoder'
                gradlew ':iabtcf-decoder:assembleRelease'

                echo 'Building Smaato Iab encoder'
                gradlew ':iabtcf-encoder:assembleRelease'

                echo 'Building Iab extras'
                gradlew ':iabtcf-extras:assembleRelease'

                echo 'Building Iab extras Jackson'
                gradlew ':iabtcf-extras-jackson:assembleRelease'

                echo 'Building Whole decoder module'
                gradlew ':tcstring-decoder:assembleRelease'

            }
        }

        stage('Upload SDK to Smaato Nexus Repository') {
            when {
                anyOf {
                    branch 'decoder_release/**'
                    branch 'develop'
                }
            }
            steps {
                gradlew 'publishReleasePublicationToSmaatoNexusRepository'
	            script {
						attachments = "[{'fallback':'NextGen Android SDK release','color':'good','title':'NextGen Android SDK Adapter release - ${BUILD_BRANCH_NAME} Uploaded to Nexus','title_link':''}]"
						slackSend (channel:'#sdk-releases',attachments:"${attachments}")
    		        }
            }
        }

        stage('Do Public Release?') {
            agent none
            when {
                branch 'decoder_release/**'
            }
            steps {
                script {
                    env.DO_PUBLIC_RELEASE = input message: 'Public Release',
                            parameters: [choice(name: 'Do Public Release?', choices: 'no\nyes', description: 'Choose "yes" if you want to publicly release this build')]
                }
            }
        }

        stage('Upload SDK to Public S3') {
            when {
                branch 'decoder_release/**'
                environment name: 'DO_PUBLIC_RELEASE', value: 'yes'
            }
            steps {
	            script {
		            	withAWS(role: 'jenkins-cfn', roleAccount: '562810932035', region: 'us-east-1') {
						attachments = "[{'fallback':'NextGen Android SDK TCString decoder release','color':'good','title':'NextGen Android SDK Adapter release - ${BUILD_BRANCH_NAME}','title_link':''}]"
						slackSend (channel:'#sdk-releases',attachments:"${attachments}")
    		            }
    		        }
                withAWS(role: 'jenkins-cfn', roleAccount: '562810932035', region: 'us-east-1') {
                    gradlew 'publishReleasePublicationToPublicRepository'
                }
            }
        }
    }
}
